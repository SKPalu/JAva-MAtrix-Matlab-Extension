/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 *
 * @author Feynman Perceptrons
 */
//public class FactorAnalysis {

//}
//import com.imsl.*;
//import com.imsl.math.Matrix;
//import com.imsl.math.SymEigen;
import java.io.Serializable;

import jamaextension.jamax.Matrix;

// Referenced classes of package com.imsl.stat:
//            SortDoubleInteger, Cdf
public class FactorAnalysis implements Serializable, Cloneable
{

    static final long serialVersionUID = 0x29d303658c630da8L;
    private double COV[][];
    private int l_nrows;
    private int l_nvariables;
    private int NF;
    private int NVAR;
    private int NDF;
    private int MAXIT;
    private double UNIQ[];
    private double EPS;
    private double EPSE;
    private int MAXSTP;

    private VarianceMethods INIT = VarianceMethods.CORRELATION_MATRIX;

    private FactorLoadingMethods IMTH = FactorLoadingMethods.PRINCIPAL_COMPONENT_MODEL;

    private FactorLoadingMethods IMTH1;

    private int ICOV;
    private double EVAL[];
    private double EVEC[][];
    private double EVEC1[][];
    private double A[][];
    private double AN;
    private double STAT[];
    private double DER[];
    private boolean GOTVECTORS;

    public FactorAnalysis(Matrix AD, int i, int j)
    {
        double ad[][] = AD.getArray();
        NDF = 100;
        MAXIT = 60;
        EPS = 0.0001D;
        EPSE = 0.10000000000000001D;
        MAXSTP = 8;
        // INIT = 1;
        // IMTH = -1;
        ICOV = 0;
        boolean flag = false;
        int k = ad.length;
        NVAR = k;
        l_nvariables = k;
        int l = 0;
        int i1 = NDF;
        int j1 = ICOV;
        double ad1[] = new double[k];
        double d = -5D;
        double d1 = -13.816000000000001D;
        double d2 = EPS;
        double d3 = EPSE;
        int l1 = MAXSTP;
        int i2 = MAXIT;
        int j2 = INIT.getNum();
        i1 = NDF;
        int k2 = IMTH.getNum();
        NF = j;
        GOTVECTORS = false;
        new Matrix(ad);// Matrix.checkMatrix(ad);
        l_nrows = ad.length;
        if (l_nrows <= 0)
        {
            Object aobj[] =
            {
                    "cov.length", new Integer(ad.length)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        l_nvariables = ad[0].length;
        if (l_nvariables <= 0)
        {
            Object aobj1[] =
            {
                    "cov[0].length", new Integer(ad[0].length)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotPositive", aobj1);
            throw new IllegalArgumentException("NotPositive");
        }
        COV = ad;
        if (i < 0 || i > 1)
        {
            Object aobj2[] =
            {
                    "matrixType", new Integer(i), new Integer(0), new Integer(3)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "OutOfRange", aobj2);
            throw new IllegalArgumentException("OutOfRange");
        }
        l = i;
        ICOV = i;
        flag = true;
        if (k < 2)
        {
            Object aobj3[] =
            {
                    "Number of variables", new Integer(k), new Integer(1)
            };
            // Messages.throwIllegalArgumentException("com.imsl.stat",
            // "NotLargeEnough", aobj3);
            throw new IllegalArgumentException("NotLargeEnough");
        }
        for (int k1 = 0; k1 < k; k1++)
        {
            if (COV[k1][k1] < 0.0D)
            {
                Object aobj4[] =
                {
                        "A diagonal element of COV", new Double(COV[k1][k1])
                };
                // Messages.throwIllegalArgumentException("com.imsl.stat",
                // "Negative", aobj4);
                throw new IllegalArgumentException("Negative");
            }
        }

    }

    public Matrix getFactorLoadings() // throws RankException,
                                      // NoDegreesOfFreedomException,
                                      // NotSemiDefiniteException,
                                      // NotPositiveSemiDefiniteException,
                                      // NotPositiveDefiniteException,
                                      // SingularException,
                                      // BadVarianceException,
                                      // EigenvalueException,
                                      // NonPositiveEigenvalueException
    {
        int i = 0;
        int l1 = NVAR;
        double d50 = -5D;
        double d51 = -13.816000000000001D;
        double ad[] = new double[l1];
        double ad1[] = new double[l1];
        double ad3[] = new double[l1];
        double ad5[][] = new double[l1][l1];
        double ad6[][] = new double[l1][NF];
        double ad7[] = new double[6];
        double ad8[] = new double[l1];
        double ad9[][] = new double[l1][l1];
        double ad10[][] = new double[l1][l1];
        double ad11[][] = new double[l1][l1];
        double ad12[] = new double[l1];
        double ad13[][] = new double[l1][l1];
        double ad14[][] = new double[l1][l1];
        double ad16[][] = new double[l1][l1];
        double ad17[] = new double[l1];
        double ad18[] = new double[l1];
        int ai[] = new int[l1];
        int ai1[] = new int[l1];
        int ai2[] = new int[1];
        if (GOTVECTORS)
        {
            return new Matrix(A);
        }
        double d20 = 0.0D;
        double d = EPS;
        double d1 = EPSE;
        int l = MAXSTP;
        int i1 = MAXIT;
        int j1 = INIT.getNum();
        int k1 = NDF;
        int i2 = IMTH.getNum();
        IMTH1 = IMTH;
        double ad4[][] = COV;
        int l24 = NF;
        if (i2 == -1)
        {
            i2 = 0;
            for (int j2 = 0; j2 < l1; j2++)
            {
                ad17[j2] = 0.0D;
            }

        }
        for (int k2 = 0; k2 < 6; k2++)
        {
            ad7[k2] = (0.0D / 0.0D);
        }

        k1 = NDF;
        int j25 = k1;
        boolean flag2 = false;
        int l21 = 0;
        double d16 = 1.7976931348623157E+308D;
        if (i == 2 && i2 != 0 && i2 != 4)
        {
            if (i2 != 5)
                ;
        }
        double d18;
        if (i2 == 4)
        {
            d18 = 1.0D;
        }
        else
        {
            d18 = 1.0D - (double) l24 / (2D * (double) l1);
        }
        for (int l2 = 0; l2 < l1; l2++)
        {
            ad3[l2] = ad4[l2][l2];
        }

        double d32 = 1.1102230246251565E-014D;
        int j = ichfac(ad4, d32, ai2, ad5);
        int i22 = ai2[0];
        if (i22 < l24)
        {
            Object aobj[] =
            {
                    new Integer(i22), new Integer(l24)
            };
            // throw new RankException("FactorAnalysis.Rank", aobj);
            throw new IllegalArgumentException("Rank");
        }
        if (j >= 3) // throw new
                    // NotPositiveSemiDefiniteException("FactorAnalysis.NotPositiveSemiDefinite",
                    // null);
        {
            throw new IllegalArgumentException("NotPositiveSemiDefinite");
        }
        double d11 = 0.0D;
        for (int i3 = 0; i3 < l1; i3++)
        {
            d11 += Math.log(ad3[i3]);
        }

        double d12 = 0.0D;
        for (int j3 = 0; j3 < l1; j3++)
        {
            if (ad5[j3][j3] > d32 * ad3[j3])
            {
                d12 += Math.log(ad5[j3][j3]);
            }
            else if (i2 != 0 && i2 != 4 && i2 != 5)
            {
                Object aobj1[] =
                {
                    new Integer(j3)
                };
                UNIQ = ad17;
                A = ad6;
                EVAL = ad12;
                EVEC = ad13;
                STAT = ad7;
                DER = ad18;
                // throw new SingularException("FactorAnalysis.Singular",
                // aobj1);
                throw new IllegalArgumentException("Singular");
            }
            else
            {
                Object aobj2[] =
                {
                    new Integer(j3)
                };
                // throw new
                // NotPositiveDefiniteException("FactorAnalysis.NotPositiveDefinite",
                // aobj2);
                throw new IllegalArgumentException("NotPositiveDefinite");
            }
        }

        for (int i17 = 0; i17 < l1; i17++)
        {
            j = i17 + 4;
            if (ad5[i17][i17] == 0.0D)
            {
                break;
            }
            ad5[i17][i17] = 1.0D / ad5[i17][i17];
            double d22 = -ad5[i17][i17];
            for (int k3 = 0; k3 < i17; k3++)
            {
                ad5[k3][i17] = ad5[k3][i17] * d22;
            }

            if (l1 > i17 + 1)
            {
                for (int l11 = i17 + 1; l11 < l1; l11++)
                {
                    double d23 = ad5[i17][l11];
                    ad5[i17][l11] = 0.0D;
                    for (int i20 = 0; i20 <= i17; i20++)
                    {
                        ad5[i20][l11] = ad5[i20][l11] + d23 * ad5[i20][i17];
                    }

                }

            }
            j = 0;
        }

        if (j > 3) // throw new
                   // NotPositiveSemiDefiniteException("FactorAnalysis.NotPositiveSemiDefinite",
                   // null);
        {
            throw new IllegalArgumentException("NotPositiveSemiDefinite");
        }
        for (int l3 = 0; l3 < l1; l3++)
        {
            for (int i12 = 0; i12 < l3; i12++)
            {
                double d24 = ad5[i12][l3];
                for (int j20 = 0; j20 <= i12; j20++)
                {
                    ad5[j20][i12] = d24 * ad5[j20][l3] + ad5[j20][i12];
                }

            }

            double d25 = ad5[l3][l3];
            for (int k20 = 0; k20 <= l3; k20++)
            {
                ad5[k20][l3] = ad5[k20][l3] * d25;
            }

        }

        for (int i4 = 0; i4 < l1; i4++)
        {
            for (int j12 = i4 + 1; j12 < l1; j12++)
            {
                ad5[j12][i4] = ad5[i4][j12];
            }

            if (j1 == 0)
            {
                if (ad5[i4][i4] != 0.0D)
                {
                    ad17[i4] = d18 / ad5[i4][i4];
                }
                else if (ad4[i4][i4] < 2.2204460492503131E-013D)
                {
                    ad17[i4] = ad4[i4][i4];
                }
                else
                {
                    ad17[i4] = ad4[i4][i4] / 1000D;
                }
            }
            else if (ad17[i4] < 0.0D || ad17[i4] > ad4[i4][i4])
            {
                Object aobj3[] =
                {
                        new Integer(i4), new Double(ad4[i4][i4]), new Double(ad17[i4])
                };
                UNIQ = ad17;
                A = ad6;
                EVAL = ad12;
                EVEC = ad13;
                STAT = ad7;
                DER = ad18;
                // throw new BadVarianceException("FactorAnalysis.BadVariance",
                // aobj3);
                throw new IllegalArgumentException("BadVariance");
            }
            if (i2 <= 1)
            {
                ad17[i4] = Math.sqrt(ad17[i4]);
                continue;
            }
            if (i2 != 5)
            {
                ad17[i4] = Math.log(ad17[i4]);
            }
        }

        int k22 = 0;
        System.arraycopy(ad17, 0, ad8, 0, l1);
        label0: do
        {
            int j22 = 0;
            double d21;
            do
            {
                for (int j4 = 0; j4 < l1; j4++)
                {
                    if (i2 <= 1 || i2 == 5)
                    {
                        for (int l20 = j4; l20 < l1; l20++)
                        {
                            ad9[j4][l20] = ad4[l20][j4];
                        }

                        if (i2 == 5)
                        {
                            ad9[j4][j4] = ad9[j4][j4] - ad17[j4];
                        }
                        else
                        {
                            ad9[j4][j4] = ad9[j4][j4] - ad17[j4] * ad17[j4];
                        }
                        continue;
                    }
                    ai[j4] = l1 - j4;
                    for (int i21 = j4; i21 < l1; i21++)
                    {
                        ad9[j4][i21] = ad5[i21][j4];
                    }

                }

                if (i2 > 1)
                {
                    for (int k4 = 0; k4 < l1; k4++)
                    {
                        double d26;
                        if (i2 == 5)
                        {
                            d26 = 1.0D / Math.sqrt(Math.max(-11D, ad4[k4][k4] - ad17[k4]));
                        }
                        else
                        {
                            d26 = Math.exp(0.5D * ad17[k4]);
                        }
                        for (int j21 = 0; j21 <= k4; j21++)
                        {
                            ad9[j21][k4] = ad9[j21][k4] * d26;
                        }

                        for (int k21 = k4; k21 < l1; k21++)
                        {
                            ad9[k4][k21] = ad9[k4][k21] * d26;
                        }

                    }

                }
                for (int l4 = 0; l4 < l1; l4++)
                {
                    ai1[l4] = l4;
                    for (int k12 = 0; k12 <= l4; k12++)
                    {
                        ad11[l4][k12] = ad9[k12][l4];
                        ad11[k12][l4] = ad9[k12][l4];
                    }

                }

                SymmetricEigen symeigen = new SymmetricEigen(ad11);
                ad12 = symeigen.getValues();
                double ad15[][] = symeigen.getVectors();
                EVEC1 = ad15;
                for (int i5 = 0; i5 < l1; i5++)
                {
                    System.arraycopy(ad15[ai1[i5]], 0, ad13[i5], 0, l1);
                }

                boolean flag3 = true;
                for (int j5 = 0; j5 < l1; j5++)
                {
                    if (Double.isNaN(ad12[j5]))
                    {
                        flag3 = false;
                    }
                }

                if (flag3)
                {
                    SortDoubleInteger.descending(ad12, ai1);
                    ad13 = new Matrix(ad13).transpose().getArray();// Matrix.transpose(ad13);
                    for (int k5 = 0; k5 < l1; k5++)
                    {
                        System.arraycopy(ad13[ai1[k5]], 0, ad16[k5], 0, l1);
                    }

                    ad13 = new Matrix(ad16).transpose().getArray();// Matrix.transpose(ad16);
                }
                else
                {
                    for (int l5 = 0; l5 < l1; l5++)
                    {
                        double d4 = ad8[l5];
                        if (i2 <= 1)
                        {
                            if (d4 < 0.0D)
                            {
                                d4 = 0.0D;
                            }
                            ad17[l5] = d4 * d4;
                            continue;
                        }
                        if (d4 < d51)
                        {
                            d4 = d51;
                        }
                        ad17[l5] = Math.exp(d4);
                    }

                    ad7[3] = ((l1 - l24) * (l1 - l24) - l1 - l24) / 2;
                    ad7[0] = d16;
                    ad7[5] = k22;
                    UNIQ = ad17;
                    A = ad6;
                    EVAL = ad12;
                    EVEC = ad13;
                    STAT = ad7;
                    DER = ad18;
                    // throw new
                    // EigenvalueException("FactorAnalysis.EigenvalueError",
                    // null);
                    throw new IllegalArgumentException("EigenvalueError");
                }
                if (i2 > 1 && i2 < 5)
                {
                    ad13 = new Matrix(ad13).transpose().getArray();// Matrix.transpose(ad13);
                    for (int i6 = 0; i6 < l1 / 2; i6++)
                    {
                        double d27 = ad12[l1 - 1 - i6];
                        double ad2[] = ad13[l1 - 1 - i6];
                        ad12[l1 - 1 - i6] = ad12[i6];
                        ad13[l1 - 1 - i6] = ad13[i6];
                        ad12[i6] = d27;
                        ad13[i6] = ad2;
                    }

                    ad13 = new Matrix(ad13).transpose().getArray();// Matrix.transpose(ad13);
                }
                if (i2 == 0 || i2 == 4)
                {
                    break label0;
                }
                if (i2 == 5)
                {
                    for (int j6 = 0; j6 < l24; j6++)
                    {
                        if (ad12[j6] <= 0.0D)
                        {
                            UNIQ = ad17;
                            A = ad6;
                            EVAL = ad12;
                            EVEC = ad13;
                            STAT = ad7;
                            DER = ad18;
                            Object aobj6[] =
                            {
                                    new Integer(k22), new Integer(j6), new Double(ad12[j6])
                            };
                            // throw new
                            // NonPositiveEigenvalueException("FactorAnalysis.NonPositiveEigenvalue",
                            // aobj6);
                            throw new IllegalArgumentException("NonPositiveEigenvalue");
                        }
                        for (int l12 = 0; l12 < l1; l12++)
                        {
                            ad13[l12][j6] = Math.sqrt(ad12[j6]) * ad13[l12][j6];
                        }

                    }

                    System.arraycopy(ad17, 0, ad8, 0, l1);
                    double d14 = 0.0D;
                    for (int k6 = 0; k6 < l1; k6++)
                    {
                        double d28 = ad17[k6];
                        double d39 = 0.0D;
                        for (int i13 = 0; i13 < l24; i13++)
                        {
                            d39 += ad13[k6][i13] * ad13[k6][i13];
                        }

                        ad17[k6] = ad4[k6][k6] - (ad4[k6][k6] - ad17[k6]) * d39;
                        d14 = Math.max(d14, Math.abs((d28 - ad17[k6]) / ad4[k6][k6]));
                    }

                    if (i != 2)
                        ;
                    if (d14 > d)
                    {
                        k22++;
                        if (k22 > i1)
                        {
                            // Warning.print(this, "com.imsl.stat",
                            // "FactorAnalysis.IterationsExceeded", null);
                            System.out.println("IterationsExceeded");
                            for (int l6 = 0; l6 < l1; l6++)
                            {
                                if (l6 < l24)
                                {
                                    ad12[l6] = ((double) j25 * (1.0D - 1.0D / ad12[l6])) / ((double) j25 - 1.0D);
                                }
                                double d29 = Math.sqrt(ad4[l6][l6] - ad17[l6]);
                                for (int j13 = 0; j13 < l24; j13++)
                                {
                                    ad13[l6][j13] = ad13[l6][j13] * d29;
                                }

                            }

                            for (int i7 = 0; i7 < l24; i7++)
                            {
                                for (int k13 = 0; k13 < l1; k13++)
                                {
                                    ad6[k13][i7] = ad13[k13][i7];
                                }

                            }

                            if ((i == 1 || i == 2) && i2 != 1 && i2 != 2)
                            {
                                if (i2 != 3)
                                    ;
                            }
                            UNIQ = ad17;
                            A = ad6;
                            EVAL = ad12;
                            EVEC = ad13;
                            STAT = ad7;
                            DER = ad18;
                            return new Matrix(ad6);
                        }
                    }
                    else
                    {
                        for (int j7 = 0; j7 < l1; j7++)
                        {
                            if (j7 < l24)
                            {
                                ad12[j7] = ((double) j25 * (1.0D - 1.0D / ad12[j7])) / ((double) j25 - 1.0D);
                            }
                            double d30 = Math.sqrt(ad4[j7][j7] - ad17[j7]);
                            for (int l13 = 0; l13 < l24; l13++)
                            {
                                ad13[j7][l13] = ad13[j7][l13] * d30;
                            }

                        }

                        for (int k7 = 0; k7 < l24; k7++)
                        {
                            for (int i14 = 0; i14 < l1; i14++)
                            {
                                ad6[i14][k7] = ad13[i14][k7];
                            }

                        }

                        if ((i == 1 || i == 2) && i2 != 1 && i2 != 2)
                        {
                            if (i2 != 3)
                                ;
                        }
                        UNIQ = ad17;
                        A = ad6;
                        EVAL = ad12;
                        EVEC = ad13;
                        STAT = ad7;
                        DER = ad18;
                        return new Matrix(ad6);
                    }
                    continue;
                }
                for (int l7 = 0; l7 < l1; l7++)
                {
                    double d34 = 0.0D;
                    for (int l22 = l24; l22 < l1; l22++)
                    {
                        double d43 = 1.0D;
                        if (i2 == 1)
                        {
                            d43 = ad12[l22];
                        }
                        else if (i2 == 2)
                        {
                            ad12[l22] = Math.min(ad12[l22], Math.sqrt(1.7976931348623156E+306D));
                            d43 = ad12[l22] * (ad12[l22] - 1.0D);
                        }
                        else if (i2 == 3)
                        {
                            d43 = 1.0D - 1.0D / ad12[l22];
                        }
                        d34 += d43 * ad13[l7][l22] * ad13[l7][l22];
                    }

                    if (i2 == 1)
                    {
                        ad18[l7] = -2D * ad17[l7] * d34;
                    }
                    else
                    {
                        ad18[l7] = d34;
                    }
                }

                double d35 = 0.0D;
                for (int i23 = l24; i23 < l1; i23++)
                {
                    if (i2 == 1)
                    {
                        d35 += ad12[i23] * ad12[i23];
                        continue;
                    }
                    if (i2 == 2)
                    {
                        d35 += (ad12[i23] - 1.0D) * (ad12[i23] - 1.0D);
                        continue;
                    }
                    if (i2 == 3)
                    {
                        d35 = d35 + 1.0D / ad12[i23] + Math.log(ad12[i23]);
                    }
                }

                double d15;
                if (i2 == 3)
                {
                    d15 = d35 + (double) (l24 - l1);
                }
                else
                {
                    d15 = 0.5D * d35;
                }
                d21 = d16;
                if (Math.abs(d16) > 1.0D)
                {
                    d21 = (d16 - d15) / d16;
                }
                else
                {
                    d21 = d16 - d15;
                }
                if (d21 >= 0.0D)
                {
                    int j23 = l21 + 1;
                    if (i != 2)
                        ;
                    d16 = d15;
                    System.arraycopy(ad17, 0, ad8, 0, l1);
                    break;
                }
                if (Math.abs(d21) <= d)
                {
                    break;
                }
                j22++;
                double d13 = Math.pow(0.5D, j22);
                if (j22 > l)
                {
                    // Warning.print(this, "com.imsl.stat",
                    // "FactorAnalysis.StepHalvingsExceeded", null);
                    System.out.println("StepHalvingsExceeded");
                    break label0;
                }
                int i8 = 0;
                while (i8 < l1)
                {
                    ad17[i8] = ad8[i8] - d13 * ad3[i8];
                    if (i2 == 1)
                    {
                        if (ad17[i8] < d50 * ad4[i8][i8])
                        {
                            ad17[i8] = d50 * ad4[i8][i8];
                        }
                    }
                    else if (ad17[i8] < d51 + Math.log(ad4[i8][i8]))
                    {
                        ad17[i8] = d51 + Math.log(ad4[i8][i8]);
                    }
                    i8++;
                }
            }
            while (true);
            k22++;
            if (k22 > i1)
            {
                // Warning.print(this, "com.imsl.stat",
                // "FactorAnalysis.IterationsExceeded", null);
                System.out.println("IterationsExceeded");
                break;
            }
            if (d21 < d)
            {
                break;
            }
            if (!flag2)
            {
                for (int j8 = 0; j8 < l1; j8++)
                {
                    for (int j14 = 0; j14 <= j8; j14++)
                    {
                        double d40 = 0.0D;
                        for (int j17 = 0; j17 < l1 - l24; j17++)
                        {
                            d40 += ad13[j8][l24 + j17] * ad13[j14][l24 + j17];
                        }

                        ad9[j14][j8] = d40 * d40;
                    }

                }

                if (i2 == 1)
                {
                    for (int k8 = 0; k8 < l1; k8++)
                    {
                        for (int k14 = k8; k14 < l1; k14++)
                        {
                            ad9[k8][k14] = ad9[k8][k14] * 2D * ad8[k8];
                        }

                        for (int l14 = 0; l14 < k8 + 1; l14++)
                        {
                            ad9[l14][k8] = ad9[l14][k8] * 2D * ad8[k8];
                        }

                    }

                }
            }
            else if (i2 == 1)
            {
                for (int l8 = 0; l8 < l1; l8++)
                {
                    double d48 = ad17[l8];
                    double d42 = d48 * d48;
                    d48 = 4D * d48;
                    for (int i15 = 0; i15 <= l8; i15++)
                    {
                        double d36 = 0.0D;
                        for (int k23 = l24; k23 < l1; k23++)
                        {
                            double d46 = 0.0D;
                            double d44 = ad12[k23];
                            for (int j24 = 0; j24 < l24; j24++)
                            {
                                d46 += (ad13[l8][j24] * ad13[i15][j24] * (d44 + ad12[j24])) / (d44 - ad12[j24]);
                            }

                            d36 += d46 * ad13[l8][k23] * ad13[i15][k23];
                        }

                        ad9[i15][l8] = d48 * ad17[i15] * d36;
                    }

                    double d37 = ad9[l8][l8];
                    for (int l23 = l24; l23 < l1; l23++)
                    {
                        d37 += 4D * (d42 - ad12[l23] * 0.5D) * ad13[l8][l23] * ad13[l8][l23];
                    }

                    ad9[l8][l8] = d37;
                }

            }
            else if (i2 == 2 || i2 == 3)
            {
                double d5;
                if (i2 == 3)
                {
                    d5 = -1D;
                }
                else
                {
                    d5 = 1.0D;
                    for (int j15 = 0; j15 < l1; j15++)
                    {
                        for (int k17 = 0; k17 < l1; k17++)
                        {
                            ad13[k17][j15] = ad13[k17][j15] * Math.sqrt(ad12[j15]);
                        }

                    }

                }
                for (int i9 = 0; i9 < l1; i9++)
                {
                    for (int k15 = 0; k15 <= i9; k15++)
                    {
                        double d45;
                        if (i2 == 2)
                        {
                            d45 = ad5[i9][k15] * Math.exp(0.5D * (ad17[i9] + ad17[k15]));
                        }
                        else if (i9 == k15)
                        {
                            d45 = 1.0D;
                        }
                        else
                        {
                            d45 = 0.0D;
                        }
                        double d38 = 0.0D;
                        for (int i24 = l24; i24 < l1; i24++)
                        {
                            double d49 = ad12[i24];
                            double d47 = d45;
                            for (int k24 = 0; k24 < l24; k24++)
                            {
                                d47 += (ad13[i9][k24] * ad13[k15][k24] * (-2D + d49 + ad12[k24])) / (d49 - ad12[k24]);
                            }

                            d38 += d47 * ad13[i9][i24] * ad13[k15][i24];
                        }

                        ad9[k15][i9] = d38;
                    }

                    ad9[i9][i9] = ad9[i9][i9] + d5 * ad18[i9];
                }

                if (i2 == 2)
                {
                    for (int j9 = 0; j9 < l1; j9++)
                    {
                        for (int l17 = 0; l17 < l1; l17++)
                        {
                            ad13[l17][j9] = (ad13[l17][j9] * 1.0D) / Math.sqrt(ad12[j9]);
                        }

                    }

                }
            }
            System.arraycopy(ad18, 0, ad3, 0, l1);
            boolean flag = false;
            int k9 = 0;
            double d19 = ad9[0][0];
            for (int i18 = 1; i18 < l1; i18++)
            {
                if (ad9[i18][i18] > d19)
                {
                    k9 = i18;
                    d19 = ad9[i18][i18];
                }
            }

            double d6 = d32 * ad9[k9][k9];
            for (int j18 = 0; j18 < l1; j18++)
            {
                ad9[j18][j18] = ad9[j18][j18] + d6;
            }

            boolean flag1 = false;
            if (flag2)
            {
                for (int l9 = 0; l9 < l1; l9++)
                {
                    if (ad9[l9][l9] < 0.0D)
                    {
                        flag1 = true;
                    }
                }

            }
            if (!flag1)
            {
                for (int i10 = 0; i10 < l1; i10++)
                {
                    for (int l15 = 0; l15 <= i10; l15++)
                    {
                        ad11[i10][l15] = ad9[l15][i10];
                        ad11[l15][i10] = ad9[l15][i10];
                    }

                }

                int k = ichfac(ad11, d32, ai2, ad9);
                if (k > 0 && !flag2)
                {
                    for (int j10 = 0; j10 < l1; j10++)
                    {
                        double d7 = ad8[j10];
                        if (i2 <= 1)
                        {
                            if (d7 < 0.0D)
                            {
                                d7 = 0.0D;
                            }
                            ad17[j10] = d7 * d7;
                            continue;
                        }
                        if (d7 < d51)
                        {
                            d7 = d51;
                        }
                        ad17[j10] = Math.exp(d7);
                    }

                    ad7[3] = ((l1 - l24) * (l1 - l24) - l1 - l24) / 2;
                    ad7[0] = d16;
                    ad7[5] = k22;
                    UNIQ = ad17;
                    A = ad6;
                    EVAL = ad12;
                    EVEC = ad13;
                    STAT = ad7;
                    DER = ad18;
                    // throw new
                    // NotSemiDefiniteException("FactorAnalysis.NotSemiDefinite",
                    // null);
                    throw new IllegalArgumentException("NotSemiDefinite");
                }
            }
            if (flag1)
            {
                if (!flag2)
                {
                    for (int k10 = 0; k10 < l1; k10++)
                    {
                        double d8 = ad8[k10];
                        if (i2 <= 1)
                        {
                            if (d8 < 0.0D)
                            {
                                d8 = 0.0D;
                            }
                            ad17[k10] = d8 * d8;
                            continue;
                        }
                        if (d8 < d51)
                        {
                            d8 = d51;
                        }
                        ad17[k10] = Math.exp(d8);
                    }

                    ad7[3] = ((l1 - l24) * (l1 - l24) - l1 - l24) / 2;
                    ad7[0] = d16;
                    ad7[5] = k22;
                    UNIQ = ad17;
                    A = ad6;
                    EVAL = ad12;
                    EVEC = ad13;
                    STAT = ad7;
                    DER = ad18;
                    // throw new
                    // NotSemiDefiniteException("FactorAnalysis.NotSemiDefinite",
                    // null);
                    throw new IllegalArgumentException("NotSemiDefinite");
                }
                flag2 = false;
            }
            ad18[0] = ad18[0] / ad9[0][0];
            if (l1 > 1)
            {
                for (int i16 = 1; i16 < l1; i16++)
                {
                    double d41 = 0.0D;
                    for (int l10 = 0; l10 < i16; l10++)
                    {
                        d41 += ad9[l10][i16] * ad18[l10];
                    }

                    ad18[i16] = ad18[i16] - d41;
                    ad18[i16] = ad18[i16] / ad9[i16][i16];
                }

            }
            ad18[l1 - 1] = ad18[l1 - 1] / ad9[l1 - 1][l1 - 1];
            if (l1 > 1)
            {
                for (int l16 = 1; l16 < l1; l16++)
                {
                    int j16 = l1 - l16;
                    double d31 = -ad18[j16];
                    for (int i11 = 0; i11 < j16; i11++)
                    {
                        ad18[i11] = ad18[i11] + d31 * ad9[i11][j16];
                    }

                    ad18[j16 - 1] = ad18[j16 - 1] / ad9[j16 - 1][j16 - 1];
                }

            }
            for (int j11 = 0; j11 < l1; j11++)
            {
                if (ad9[j11][j11] == 0.0D)
                {
                    ad18[j11] = 0.0D;
                }
                ad3[j11] = ad18[j11];
                ad17[j11] = ad8[j11] - ad18[j11];
                if (i2 == 1)
                {
                    if (ad17[j11] < d50 * ad4[j11][j11])
                    {
                        ad17[j11] = d50 * ad4[j11][j11];
                    }
                    continue;
                }
                if (ad17[j11] < d51 + Math.log(ad4[j11][j11]))
                {
                    ad17[j11] = d51 + Math.log(ad4[j11][j11]);
                }
            }

            l21 = ((flag2) ? 1 : 0);
            if (d21 < d1)
            {
                flag2 = true;
            }
            else
            {
                flag2 = false;
            }
        }
        while (true);
        for (int k16 = 0; k16 < l24; k16++)
        {
            double d9 = 0.0D;
            for (int k18 = 0; k18 < l1; k18++)
            {
                d9 += ad13[k18][k16] * ad13[k18][k16];
            }

            d9 = 1.0D / Math.sqrt(d9);
            if (ad12[k16] <= 0.0D)
            {
                UNIQ = ad17;
                A = ad6;
                EVAL = ad12;
                EVEC = ad13;
                STAT = ad7;
                DER = ad18;
                Object aobj4[] =
                {
                        new Integer(k22), new Integer(k16), new Double(ad12[k16])
                };
                // throw new
                // NonPositiveEigenvalueException("FactorAnalysis.NonPositiveEigenvalue",
                // aobj4);
                throw new IllegalArgumentException("NonPositiveEigenvalue");
            }
            if (i2 <= 1)
            {
                d9 *= Math.sqrt(ad12[k16]);
            }
            else if (i2 == 4)
            {
                d9 = d9 * (1.0D / ad12[k16] - 1.0D) * Math.sqrt(ad12[k16]);
            }
            else
            {
                d9 *= Math.sqrt(1.0D / ad12[k16] - 1.0D);
            }
            for (int l18 = 0; l18 < l1; l18++)
            {
                ad13[l18][k16] = ad13[l18][k16] * d9;
            }

            for (int i19 = 0; i19 < l1; i19++)
            {
                ad6[i19][k16] = ad13[i19][k16];
            }

        }

        if (i2 > 1)
        {
            for (int k11 = 0; k11 < l1; k11++)
            {
                double d10 = Math.exp(0.5D * ad17[k11]);
                ad3[k11] = d10;
                for (int j19 = 0; j19 < l24; j19++)
                {
                    ad6[k11][j19] = ad6[k11][j19] * d10;
                }

            }

            for (int k19 = 0; k19 < l1; k19++)
            {
                ad17[k19] = ad3[k19] * ad3[k19];
            }

        }
        else if (i2 <= 1)
        {
            for (int l19 = 0; l19 < l1; l19++)
            {
                ad17[l19] = ad17[l19] * ad17[l19];
            }

        }
        if (i2 != 0 && i2 != 4)
        {
            ad7[0] = d16;
            int i25 = ((l1 - l24) * (l1 - l24) - l1 - l24) / 2;
            double d33 = i25;
            if (i25 <= 0)
            {
                Object aobj5[] =
                {
                        new Integer(NVAR), new Integer(NF)
                };
                // throw new
                // NoDegreesOfFreedomException("FactorAnalysis.NoDegreesOfFreedom",
                // aobj5);
                throw new IllegalArgumentException("NoDegreesOfFreedom");
            }
            double d2 = (double) j25 - (double) (l1 + l1 + 5) / 6D;
            double d3 = (d2 - (double) (2 * l24) / 3D) * d16;
            double d17 = -(d12 - 0.5D * d11) / (0.5D * (double) (l1 - 1) * (double) l1);
            d17 = (d17 - d16 / d33) / (d17 - 3D / (d2 - (double) (2 * l24)));
            if (d17 > 1.0D)
            {
                d17 = 1.0D;
            }
            ad7[1] = d17;
            ad7[2] = d3;
            ad7[3] = d33;
            ad7[4] = 1.0D - Cdf.chi(d3, d33);
            ad7[5] = k22;
        }
        UNIQ = ad17;
        A = ad6;
        EVAL = ad12;
        EVEC = ad13;
        STAT = ad7;
        DER = ad18;
        GOTVECTORS = true;
        if (i != 1)
        {
            if (i != 2)
                ;
        }
        return new Matrix(ad6);
    }

    public void setDegreesOfFreedom(int i)
    {
        if (i <= 0)
        {
            Object aobj[] =
            {
                    "DegreesOfFreedom", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        else
        {
            NDF = i;
        }
    }

    public void setVariances(Matrix AD)
    {
        if (AD != null)
        {
            if (!AD.isVector())
            {
                throw new IllegalArgumentException("Parameter \"AD\" must be a vector and not a matrix.");
            }
            UNIQ = AD.getRowPackedCopy();
        }

    }

    public void setConvergenceCriterion1(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "ConvergenceCriterion1", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        else
        {
            EPS = d;
        }
    }

    public void setConvergenceCriterion2(double d)
    {
        if (d <= 0.0D)
        {
            Object aobj[] =
            {
                    "ConvergenceCriterion2", new Double(d)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        else
        {
            EPSE = d;
        }
    }

    public void setMaxStep(int i)
    {
        if (i <= 0)
        {
            Object aobj[] =
            {
                    "MaxStep", new Integer(i)
            };
            // Messages.throwIllegalArgumentException("com.imsl.math",
            // "NotPositive", aobj);
            throw new IllegalArgumentException("NotPositive");
        }
        else
        {
            MAXSTP = i;
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
            throw new IllegalArgumentException("NotPositive");
        }
        else
        {
            MAXIT = i;
        }
    }

    public void setVarianceEstimationMethod(VarianceMethods i)
    {
        /*
         * if (i < 0 || i > 1) { Object aobj[] = { "VarianceEstimationMethod",
         * new Integer(i), new Integer(0), new Integer(1) };
         * //Messages.throwIllegalArgumentException("com.imsl.stat",
         * "OutOfRange", aobj); throw new
         * IllegalArgumentException("OutOfRange"); } else { INIT = i; }
         */
        if (i == null)
        {
            return;
        }
        INIT = i;
    }

    public void setFactorLoadingEstimationMethod(FactorLoadingMethods i)
    {
        /*
         * int j = i; if (j < -1 || j > 5) { Object aobj[] = {
         * "FactorLoadingEstimationMethod", new Integer(j), new Integer(-1), new
         * Integer(5) };
         * //Messages.throwIllegalArgumentException("com.imsl.stat",
         * "OutOfRange", aobj); throw new
         * IllegalArgumentException("OutOfRange"); } else { IMTH = j; }
         */
        // FactorLoadingMethods facLoadMethod
        if (i == null)
        {
            return;
        }
        IMTH = i;
    }

    public Matrix getVariances() // throws RankException,
                                 // NoDegreesOfFreedomException,
                                 // NotSemiDefiniteException,
                                 // NotPositiveSemiDefiniteException,
                                 // NotPositiveDefiniteException,
                                 // SingularException, BadVarianceException,
                                 // EigenvalueException,
                                 // NonPositiveEigenvalueException
    {
        if (!GOTVECTORS)
        {
            getFactorLoadings();
        }
        return new Matrix(UNIQ);
    }

    public Matrix getValues() // throws RankException,
                              // NoDegreesOfFreedomException,
                              // NotSemiDefiniteException,
                              // NotPositiveSemiDefiniteException,
                              // NotPositiveDefiniteException,
                              // SingularException, BadVarianceException,
                              // EigenvalueException,
                              // NonPositiveEigenvalueException
    {
        if (!GOTVECTORS)
        {
            getFactorLoadings();
        }
        return new Matrix(EVAL);
    }

    public Matrix getVectors() // throws RankException,
                               // NoDegreesOfFreedomException,
                               // NotSemiDefiniteException,
                               // NotPositiveSemiDefiniteException,
                               // NotPositiveDefiniteException,
                               // SingularException, BadVarianceException,
                               // EigenvalueException,
                               // NonPositiveEigenvalueException
    {
        if (!GOTVECTORS)
        {
            getFactorLoadings();
        }
        if (IMTH1.getNum() == -1)
        {
            return new Matrix(EVEC1);
        }
        else
        {
            return new Matrix(EVEC);
        }
    }

    public Matrix getStatistics() // throws RankException,
                                  // NoDegreesOfFreedomException,
                                  // NotSemiDefiniteException,
                                  // NotPositiveSemiDefiniteException,
                                  // NotPositiveDefiniteException,
                                  // SingularException, BadVarianceException,
                                  // EigenvalueException,
                                  // NonPositiveEigenvalueException
    {
        if (!GOTVECTORS)
        {
            getFactorLoadings();
        }
        return new Matrix(STAT);
    }

    public Matrix getParameterUpdates() // throws RankException,
                                        // NoDegreesOfFreedomException,
                                        // NotSemiDefiniteException,
                                        // NotPositiveSemiDefiniteException,
                                        // NotPositiveDefiniteException,
                                        // SingularException,
                                        // BadVarianceException,
                                        // EigenvalueException,
                                        // NonPositiveEigenvalueException
    {
        if (!GOTVECTORS)
        {
            getFactorLoadings();
        }
        return new Matrix(DER);
    }

    public Matrix getPercents() // throws RankException,
                                // NoDegreesOfFreedomException,
                                // NotSemiDefiniteException,
                                // NotPositiveSemiDefiniteException,
                                // NotPositiveDefiniteException,
                                // SingularException, BadVarianceException,
                                // EigenvalueException,
                                // NonPositiveEigenvalueException
    {
        int i = NVAR;
        double ad[] = new double[i];
        if (!GOTVECTORS)
        {
            getFactorLoadings();
        }
        double d1 = 0.0D;
        for (int j = 0; j < i; j++)
        {
            d1 += EVAL[j];
        }

        double d = 0.0D;
        for (int k = 0; k < i; k++)
        {
            d += EVAL[k];
            ad[k] = d / d1;
        }

        return new Matrix(ad);
    }

    public Matrix getStandardErrors() // throws RankException,
                                      // NoDegreesOfFreedomException,
                                      // NotSemiDefiniteException,
                                      // NotPositiveSemiDefiniteException,
                                      // NotPositiveDefiniteException,
                                      // SingularException,
                                      // BadVarianceException,
                                      // EigenvalueException,
                                      // NonPositiveEigenvalueException
    {
        int i = NVAR;
        int i2 = ICOV;
        double ad[][] = new double[i][i];
        double ad1[] = new double[i];
        if (!GOTVECTORS)
        {
            getFactorLoadings();
        }
        double d = Math.sqrt(2D / (double) NDF);
        if (i2 == 0)
        {
            for (int j = 0; j < i; j++)
            {
                double d2 = EVAL[j];
                if (d2 < 0.0D)
                {
                    d2 = 0.0D;
                }
                ad1[j] = d2 * d;
            }

        }
        else
        {
            for (int k = 0; k < i; k++)
            {
                double d3 = EVAL[k];
                if (d3 < 0.0D)
                {
                    d3 = 0.0D;
                }
                ad1[k] = d3 * d3;
                for (int i1 = 0; i1 < i; i1++)
                {
                    ad[i1][k] = EVEC1[i1][k] * EVEC1[i1][k];
                }

                double d1 = 0.0D;
                for (int j1 = 0; j1 < i; j1++)
                {
                    d1 += ad[j1][k] * ad[j1][k];
                }

                ad1[k] = ad1[k] + (1.0D - 2D * d3) * d1;
            }

            for (int l = 0; l < i; l++)
            {
                for (int k1 = 0; k1 < i; k1++)
                {
                    for (int l1 = k1 + 1; l1 < i; l1++)
                    {
                        ad1[l] = ad1[l] + 2D * ad[k1][l] * ad[l1][l] * COV[k1][l1] * COV[k1][l1];
                    }

                }

                ad1[l] = Math.sqrt(Math.max(0.0D, ad1[l])) * d;
            }

        }
        return new Matrix(ad1);
    }

    public Matrix getCorrelations()
    { // throws RankException, NoDegreesOfFreedomException,
      // NotSemiDefiniteException, NotPositiveSemiDefiniteException,
      // NotPositiveDefiniteException, SingularException, BadVarianceException,
      // EigenvalueException, NonPositiveEigenvalueException

        int i = NVAR;
        int k1 = ICOV;
        double ad[][] = new double[i][i];
        if (!GOTVECTORS)
        {
            getFactorLoadings();
        }
        if (k1 == 0)
        {
            for (int j = 0; j < i; j++)
            {
                if (COV[j][j] > 0.0D)
                {
                    for (int l = 0; l < i; l++)
                    {
                        ad[l][j] = (EVEC1[l][j] * Math.sqrt(Math.max(0.0D, EVAL[j])) * 1.0D) / Math.sqrt(COV[j][j]);
                    }

                }
                else
                {
                    for (int i1 = 0; i1 < i; i1++)
                    {
                        ad[i1][j] = (0.0D / 0.0D);
                    }

                }
            }

        }
        else
        {
            for (int k = 0; k < i; k++)
            {
                for (int j1 = 0; j1 < i; j1++)
                {
                    ad[j1][k] = EVEC1[j1][k] * Math.sqrt(Math.max(0.0D, EVAL[k]));
                }

            }

        }
        return new Matrix(ad);
    }

    private int ichfac(double ad[][], double d, int ai[], double ad1[][])
    {
        int l1 = 0;
        int i = NVAR;
        int j = 0;
        ai[0] = 0;
        for (int i1 = 0; i1 < i; i1++)
        {
            System.arraycopy(ad[i1], 0, ad1[i1], 0, i);
        }

        ai[0] = 0;
        for (int j1 = 0; j1 < i; j1++)
        {
            double d1 = 0.0D;
            double d2 = d * Math.sqrt(Math.abs(ad1[j1][0]));
            for (int k1 = 0; k1 < j1; k1++)
            {
                double d3 = 0.0D;
                for (int k = 0; k < k1; k++)
                {
                    d3 += ad1[k][k1] * ad1[k][j1];
                }

                double d5 = ad1[k1][j1] - d3;
                if (ad1[k1][k1] != 0.0D)
                {
                    d5 /= ad1[k1][k1];
                    ad1[k1][j1] = d5;
                    d1 += d5 * d5;
                    continue;
                }
                if (l1 == 0)
                {
                    double d4 = 0.0D;
                    for (int l = 0; l < k1; l++)
                    {
                        d4 += ad1[l][k1] * ad1[l][k1];
                    }

                    d4 = Math.sqrt(d4);
                    if (Math.abs(d5) > d2 * d4)
                    {
                        l1 = j1 + 1;
                    }
                }
                ad1[k1][j1] = 0.0D;
            }

            d1 = ad1[j1][j1] - d1;
            if (Math.abs(d1) <= d * Math.abs(ad1[j1][j1]))
            {
                d1 = 0.0D;
            }
            else if (d1 < 0.0D)
            {
                d1 = 0.0D;
                if (l1 == 0)
                {
                    l1 = j1 + 1;
                }
            }
            else
            {
                ai[0] = ai[0] + 1;
            }
            ad1[j1][j1] = Math.sqrt(d1);
        }

        j = l1;
        return j;
    }
}
