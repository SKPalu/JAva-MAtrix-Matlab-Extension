/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

import jamaextension.jamax.Matrix;
import jamaextension.jamax.datafun.QuickSort;
import jamaextension.jamax.datafun.QuickSortMat;

/**
 * 
 * @author Feynman Perceptrons
 */
public class BsInterpolate extends BSpline
{

    static final long serialVersionUID = 0x5f698ec2fcad107dL;

    public BsInterpolate(Matrix ad, Matrix ad1)
    {
        this(ad, ad1, 4);
    }

    public BsInterpolate(Matrix A, Matrix B, int i)
    {

        if (A == null)
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"A\" must be a vector and not a matrix.");
        }
        double ad[] = A.getRowPackedCopy();

        if (B == null)
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"B\" must be non-null.");
        }
        if (!B.isVector())
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"B\" must be a vector and not a matrix.");
        }
        double ad1[] = B.getRowPackedCopy();

        order = i;
        knot = b2nak(ad.length, ad, i);
        coef = new double[ad.length];
        b2int(ad.length, ad, ad1, i, knot, coef);
    }

    public BsInterpolate(Matrix A, Matrix B, int i, Matrix C)
    {
        if (A == null)
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"A\" must be non-null.");
        }
        if (!A.isVector())
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"A\" must be a vector and not a matrix.");
        }
        double ad[] = A.getRowPackedCopy();

        if (B == null)
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"B\" must be non-null.");
        }
        if (!B.isVector())
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"B\" must be a vector and not a matrix.");
        }
        double ad1[] = B.getRowPackedCopy();

        if (C == null)
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"C\" must be non-null.");
        }
        if (!C.isVector())
        {
            throw new IllegalArgumentException("BsInterpolate : Parameter \"C\" must be a vector and not a matrix.");
        }
        double ad2[] = C.getRowPackedCopy();

        order = i;
        knot = new double[ad2.length];
        for (int j = 0; j < ad2.length; j++)
        {
            knot[j] = ad2[j];
        }

        coef = new double[ad.length];
        b2int(ad.length, ad, ad1, order, knot, coef);
    }

    private void b2int(int i, double ad[], double ad1[], int j, double ad2[], double ad3[])
    {
        double ad4[] = new double[(5 * j - 2) * i];
        double ad5[] = new double[i];
        double ad6[] = new double[i];
        double ad7[] = new double[(3 * j - 1) * i];
        int ai[] = new int[i];
        if (ad.length != ad1.length)
        {
            Object aobj[] =
            {
                    "xData", new Integer(ad.length), "yData", new Integer(ad1.length)
            };
            throw new IllegalArgumentException("com.imsl.math : UnequalLengths");
        }
        if (knot.length != order + ad.length)
        {
            Object aobj1[] =
            {
                    "knot", new Integer(knot.length), new Integer(order + ad.length)
            };
            throw new IllegalArgumentException("com.imsl.math : NotEqual");
        }
        b3int(j, ad2, i);
        for (int l = 0; l < i; l++)
        {
            ai[l] = l;
            ad5[l] = ad[l];
        }

        // Sort.ascending(ad5, ai); //===> Replace by the following block of
        // codes
        // ----------------------------------------------
        Matrix ad5_mat = new Matrix(ad5);
        // SortMat sort = new SortMat(ad5_mat);

        // ----- Changed on 12/08/2009 from 'SortMat' into 'QuickSortMat'
        QuickSort sort = new QuickSortMat(ad5_mat, true, true); // --> new line

        double[][] vec = ((Matrix) sort.getSortedObject()).toRowVector().getArray();
        ad5 = vec[0];
        int[][] perm = sort.getIndices().toRowVector().getArray();
        ai = perm[0];
        // -----------------------------------------------

        ad6[0] = ad1[ai[0]];
        for (int i1 = 2; i1 <= i; i1++)
        {
            ad6[i1 - 1] = ad1[ai[i1 - 1]];
            if (ad5[i1 - 2] == ad5[i1 - 1])
            {
                Object aobj2[] =
                {
                        new Integer(ai[i1 - 2] - 1), new Integer(ai[i1 - 1] - 1), new Double(ad5[i1 - 1])
                };
                System.out.println("com.imsl.math : BSpline.XDataDistinct");
                return;
            }
        }

        c1not("X", "KORDER", i, ad5, j, ad2);
        int j1 = 1;
        int k1 = j1 + (2 * j - 1) * i;
        int k = 2 * j - 1;
        b5int(i, ad5, ad6, j, ad2, ad3, ad4, k, ad7, ai);
    }

    private void b5int(int i, double ad[], double ad1[], int j, double ad2[], double ad3[], double ad4[], int k,
            double ad5[], int ai[])
    {
        double ad6[] = new double[j];
        double ad7[] = new double[(3 * j - 2) * i];
        for (int l = 0; l < k * i; l++)
        {
            ad4[l] = 0.0D;
        }

        int i2 = j;
        for (int i1 = 1; i1 <= i; i1++)
        {
            double d = ad[i1 - 1];
            i2 = Math.max(i2, i1);
            do
            {
                if (d < ad2[i2])
                {
                    break;
                }
                if (++i2 < Math.min(i1 + j, i + 1))
                {
                    continue;
                }
                i2--;
                break;
            }
            while (true);
            b4int(ad2, j, d, i2, ad3, ad5, ad6);
            for (int j2 = 0; j2 < j; j2++)
            {
                ad4[(i2 - j) * k + (((i1 - i2) + j * 2) - 2) + j2 * (k - 1)] = ad3[j2];
            }

        }

        int j1 = j - 1;
        int k1 = j - 1;
        int l1 = 1;
        l2lrb(i, ad4, k, j1, k1, ad1, l1, ad3, ad7, ai, ad5);
    }

    private double[] b2nak(int i, double ad[], int j)
    {
        double ad1[] = new double[i];
        double ad2[] = new double[i + j];
        int ai[] = new int[i];
        if (j <= 1)
        {
            Object aobj[] =
            {
                new Integer(j)
            };
            System.out.println("com.imsl.math : BSpline.OrderLessThan2");
            return ad2;
        }
        if (i < j)
        {
            Object aobj1[] =
            {
                    new Integer(i), new Integer(j)
            };
            System.out.println("com.imsl.math : BSpline.xDataLength");
            return ad2;
        }
        for (int k = 1; k <= i; k++)
        {
            ai[k - 1] = k;
            ad1[k - 1] = ad[k - 1];
        }

        // Sort.ascending(ad1, ai);//===> Replace by the following block of
        // codes
        // ----------------------------------------------
        Matrix ad5_mat = new Matrix(ad1);
        // SortMat sort = new SortMat(ad5_mat);

        // ----- Changed on 12/08/2009 from 'SortMat' into 'QuickSortMat'
        QuickSort sort = new QuickSortMat(ad5_mat, true, true); // --> new line

        double[][] vec = ((Matrix) sort.getSortedObject()).toRowVector().getArray();
        ad1 = vec[0];
        int[][] perm = sort.getIndices().toRowVector().getArray();
        ai = perm[0];
        // -----------------------------------------------

        for (int l = 2; l <= i; l++)
        {
            if (ad1[l - 2] == ad1[l - 1])
            {
                Object aobj2[] =
                {
                        new Integer(ai[l - 2] - 1), new Integer(ai[l - 1] - 1), new Double(ad1[l - 1])
                };
                System.out.println("com.imsl.math : BSpline.XDataDistinct");
                return ad2;
            }
        }

        for (int i1 = 1; i1 <= j; i1++)
        {
            ad2[i1 - 1] = ad1[0];
            ad2[(i + i1) - 1] = ad1[i - 1] * (1.0D + sign(1.0D, ad1[i - 1]) * 2.2204460492503E-016D * 100D);
            if (ad1[i - 1] == 0.0D)
            {
                ad2[(i + i1) - 1] = ad1[i - 1] + 2.2204460492503001E-014D;
            }
        }

        if (j % 2 == 0)
        {
            for (int j1 = 0; j1 < i - j; j1++)
            {
                ad2[j + j1] = ad1[j / 2 + j1];
            }

        }
        else
        {
            for (int k1 = j + 1; k1 <= i; k1++)
            {
                ad2[k1 - 1] = 0.5D * (ad1[k1 - j / 2 - 2] + ad1[k1 - j / 2 - 1]);
            }

        }
        return ad2;
    }

    private void c1not(String s, String s1, int i, double ad[], int j, double ad1[])
    {
        StringBuffer stringbuffer = new StringBuffer("");
        if (s1.equals("KORDER"))
        {
            stringbuffer = new StringBuffer("NDATA");
        }
        else
        {
            StringBuffer stringbuffer1 = new StringBuffer("N");
            stringbuffer1.append(s);
            stringbuffer1.append("DATA");
        }
        if (j <= 0)
        {
            Object aobj[] =
            {
                    "order", new Integer(j)
            };
            System.out.println("com.imsl.math : NotPositive");
            return;
        }
        if (i < j)
        {
            Object aobj1[] =
            {
                    new Integer(i), new Integer(j)
            };
            System.out.println("com.imsl.math : BSpline.xDataLength");
            return;
        }
        int i1 = 1;
        for (int k = 2; k <= i + j; k++)
        {
            if (ad1[k - 1] == ad1[k - 2])
            {
                if (++i1 > j)
                {
                    Object aobj2[] =
                    {
                            "X", new Integer((k - 1 - i1) + 1), new Integer(k - 1)
                    };
                    System.out.println("com.imsl.math : BSpline.KnotMultiplicity");
                    return;
                }
                continue;
            }
            if (ad1[k - 1] < ad1[k - 2])
            {
                Object aobj3[] =
                {
                        "X", new Integer(k - 2), new Double(ad1[k - 2]), new Integer(k - 1), new Double(ad1[k - 1])
                };
                System.out.println("com.imsl.math : BSpline.KnotsNotIncreasing");
                return;
            }
            i1 = 1;
        }

        if (ad[0] < ad1[j] && ad[0] >= ad1[0])
        {
            for (int l = 2; l <= i - 1; l++)
            {
                if (ad[l - 1] >= ad1[(l + j) - 1] || ad[l - 1] < ad1[l - 1])
                {
                    double d = ad[l - 1];
                    double d3 = ad1[l - 1];
                    double d6 = ad1[(l + j) - 1];
                    Object aobj4[] =
                    {
                            new Integer(l - 1), "X", new Double(d), new Integer((j + l) - 1), new Double(d3),
                            new Double(d6)
                    };
                    System.out.println("com.imsl.math : BSpline.KnotDataInterlacing");
                    return;
                }
            }

            if (ad[i - 1] > ad1[(i + j) - 1] || ad[i - 1] <= ad1[i - 1])
            {
                double d1 = ad[i - 1];
                double d4 = ad1[i - 1];
                double d7 = ad1[(i + j) - 1];
                Object aobj5[] =
                {
                        "X", new Double(d1), new Double(d4), new Double(d7)
                };
                System.out.println("com.imsl.math : BSpline.DataTooLarge");
                return;
            }
        }
        else
        {
            double d2 = ad[0];
            double d5 = ad1[0];
            double d8 = ad1[j];
            Object aobj6[] =
            {
                    "X", new Double(d2), new Double(d5), new Double(d8)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.DataTooSmall");
        }
    }

    private void crbrb(int i, double ad[], int j, int k, int l, double ad1[], int i1, int j1, int k1)
    {
        int k6 = l + k + 1;
        if (i < 1)
        {
            Object aobj[] =
            {
                new Integer(i)
            };
            System.out.println("com.imsl.math : BSpline.MatrixOrderTooSmall");
            return;
        }
        if (j < k6)
        {
            Object aobj1[] =
            {
                new Integer(j)
            };
            System.out.println("com.imsl.math : BSpline.LDATooSmall");
            return;
        }
        if (k < 0 || k >= i)
        {
            Object aobj2[] =
            {
                    new Integer(k), new Integer(i)
            };
            System.out.println("com.imsl.math : BSpline.BadNLCA");
            return;
        }
        if (l < 0 || l >= i)
        {
            Object aobj3[] =
            {
                    new Integer(l), new Integer(i)
            };
            System.out.println("com.imsl.math : BSpline.BadNUCA");
            return;
        }
        if (i1 < j1 + k1 + 1)
        {
            Object aobj4[] =
            {
                    new Integer(i1), new Integer(j1), new Integer(k1), new Integer(j1 + k1 + 1)
            };
            System.out.println("com.imsl.math : BSpline.BadLDB");
            return;
        }
        if (j1 < k || j1 >= i)
        {
            Object aobj5[] =
            {
                    new Integer(j1), new Integer(k), new Integer(i)
            };
            System.out.println("com.imsl.math : BSpline.BadNLCB");
            return;
        }
        if (k1 < l || k1 >= i)
        {
            Object aobj6[] =
            {
                    new Integer(k1), new Integer(l), new Integer(i)
            };
            System.out.println("com.imsl.math : BSpline.BadNUCB");
            return;
        }
        if (j > i1)
        {
            for (int l3 = 1; l3 <= i; l3++)
            {
                int i6 = Math.min(l3 - 1, l);
                int k5 = Math.min(i - l3, k);
                int i5 = (l - i6) + 1;
                for (int l1 = 0; l1 < i6 + k5 + 1; l1++)
                {
                    ad1[(l3 - 1) * i1 + (i5 - 1) + l1] = ad[(l3 - 1) * j + (i5 - 1) + l1];
                }

                if (l - i6 != 0)
                {
                    for (int i2 = 0; i2 < l - i6; i2++)
                    {
                        ad1[(l3 - 1) * i1 + i2] = 0.0D;
                    }

                }
                if (k - k5 != 0)
                {
                    for (int j2 = 0; j2 < k - k5; j2++)
                    {
                        ad1[(l3 - 1) * i1 + (l + k5 + 1) + j2] = 0.0D;
                    }

                }
            }

        }
        else
        {
            for (int i4 = i; i4 >= 1; i4--)
            {
                int j6 = Math.min(i4 - 1, l);
                int l5 = Math.min(i - i4, k);
                int j5 = (l - j6) + 1;
                int l6 = 0;
                int i7 = 0;
                byte byte0 = -1;
                byte byte1 = -1;
                int j7 = j6 + l5 + 1;
                if (byte0 < 0)
                {
                    l6 = (-j7 + 1) * byte0;
                }
                if (byte1 < 0)
                {
                    i7 = (-j7 + 1) * byte1;
                }
                for (int k7 = 0; k7 < j7; k7++)
                {
                    ad1[(i4 - 1) * i1 + (j5 - 1) + i7] = ad[(i4 - 1) * j + (j5 - 1) + l6];
                    l6 += byte0;
                    i7 += byte1;
                }

                if (l - j6 != 0)
                {
                    for (int k2 = 0; k2 < l - j6; k2++)
                    {
                        ad1[(i4 - 1) * i1 + k2] = 0.0D;
                    }

                }
                if (k - l5 == 0)
                {
                    continue;
                }
                for (int l2 = 0; l2 < k - l5; l2++)
                {
                    ad1[(i4 - 1) * i1 + (l + l5 + 1) + l2] = 0.0D;
                }

            }

        }
        if (k1 > l)
        {
            for (int j4 = k6; j4 >= 1; j4--)
            {
                for (int i3 = 0; i3 < i; i3++)
                {
                    ad1[0 * i1 + (((k1 - l) + j4) - 1) + i3 * i1] = ad1[0 * j + (j4 - 1) + i3 * i1];
                }

            }

        }
        for (int k4 = 1; k4 <= k1 - l; k4++)
        {
            for (int j3 = 0; j3 < i; j3++)
            {
                ad1[0 * i1 + (k4 - 1) + j3 * i1] = 0.0D;
            }

        }

        for (int l4 = 1; l4 <= j1 - k; l4++)
        {
            for (int k3 = 0; k3 < i; k3++)
            {
                ad1[0 * i1 + (((k1 - l) + k6 + l4) - 1) + k3 * i1] = 0.0D;
            }

        }

    }

    private void l2crb(int i, double ad[], int j, int k, int l, double ad1[], int i1, int ai[], double ad2[],
            double ad3[])
    {
        double ad4[] = new double[1];
        int l9 = k + l + 1;
        int i10 = 2 * k + l + 1;
        if (i <= 0)
        {
            Object aobj[] =
            {
                new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.MatrixOrderTooSmall");
        }
        else if (k < 0 || k >= i)
        {
            Object aobj1[] =
            {
                    new Integer(k), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNLCA");
        }
        else if (l < 0 || l >= i)
        {
            Object aobj2[] =
            {
                    new Integer(l), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNUCA");
        }
        else if (l9 > j)
        {
            Object aobj3[] =
            {
                    new Integer(l9), new Integer(j)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.TooManyRowsInA");
        }
        else if (i10 > i1)
        {
            Object aobj4[] =
            {
                    new Integer(i10), new Integer(i1)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.TooManyRowsInFac");
        }
        ad2[0] = 0.0D;
        nr1rb(i, ad, j, k, l, ad4);
        l2trb(i, ad, j, k, l, ad1, i1, ai, ad3);
        double d1 = 1.0D;
        for (int j1 = 0; j1 < i; j1++)
        {
            ad3[j1] = 0.0D;
        }

        int j9 = k + l + 1;
        int j6 = 0;
        double d6 = 4.9406564584124654E-324D;
        double d = 1.7976931348623157E+308D;
        if (d6 * d < 1.0D)
        {
            d6 = 1.0D / d;
        }
        for (int k6 = 1; k6 <= i; k6++)
        {
            if (ad3[k6 - 1] != 0.0D)
            {
                d1 = sign(d1, -ad3[k6 - 1]);
            }
            if (Math.abs(d1 - ad3[k6 - 1]) > Math.abs(ad1[((k6 - 1) * i1 + j9) - 1])
                    && Math.abs(d1 - ad3[k6 - 1]) > Math.abs(ad1[((k6 - 1) * i1 + j9) - 1]))
            {
                double d2 = Math.abs(ad1[((k6 - 1) * i1 + j9) - 1]) / Math.abs(d1 - ad3[k6 - 1]);
                for (int k1 = 0; k1 < i; k1++)
                {
                    ad3[k1] = ad3[k1] * d2;
                }

                d1 *= d2;
            }
            double d11 = d1 - ad3[k6 - 1];
            double d12 = -d1 - ad3[k6 - 1];
            double d3 = Math.abs(d11);
            double d5 = Math.abs(d12);
            if (Math.abs(ad1[((k6 - 1) * i1 + j9) - 1]) > d6)
            {
                d11 /= ad1[((k6 - 1) * i1 + j9) - 1];
                d12 /= ad1[((k6 - 1) * i1 + j9) - 1];
            }
            else
            {
                d11 = 1.0D;
                d12 = 1.0D;
            }
            j6 = Math.min(Math.max(j6, l + ai[k6 - 1]), i);
            int k9 = j9;
            if (k6 + 1 <= j6)
            {
                for (int i6 = k6 + 1; i6 <= j6; i6++)
                {
                    k9--;
                    d5 += Math.abs(ad3[i6 - 1] + d12 * ad1[((i6 - 1) * i1 + k9) - 1]);
                }

                if (j9 > 1)
                {
                    for (int l1 = 0; l1 < j6 - k6; l1++)
                    {
                        ad3[k6 + l1] = d11 * ad1[((k6 * i1 + j9) - 2) + l1 * (i1 - 1)] + ad3[k6 + l1];
                    }

                }
                double d14 = 0.0D;
                for (int i2 = 0; i2 < j6 - k6; i2++)
                {
                    d14 += Math.abs(ad3[k6 + i2]);
                }

                d3 += d14;
                if (d3 < d5)
                {
                    double d7 = d12 - d11;
                    d11 = d12;
                    if (j9 > 1)
                    {
                        for (int j2 = 0; j2 < j6 - k6; j2++)
                        {
                            ad3[k6 + j2] = d7 * ad1[((k6 * i1 + j9) - 2) + j2 * (i1 - 1)] + ad3[k6 + j2];
                        }

                    }
                }
            }
            ad3[k6 - 1] = d11;
        }

        double d15 = 0.0D;
        for (int k2 = 0; k2 < i; k2++)
        {
            d15 += Math.abs(ad3[k2]);
        }

        double d4 = 1.0D / d15;
        for (int l2 = 0; l2 < i; l2++)
        {
            ad3[l2] = ad3[l2] * d4;
        }

        for (int l6 = i; l6 >= 1; l6--)
        {
            int j8 = Math.min(k, i - l6);
            if (l6 < i && j8 > 0)
            {
                double d16 = 0.0D;
                for (int i3 = 0; i3 < j8; i3++)
                {
                    d16 += ad1[(l6 - 1) * i1 + j9 + i3] * ad3[l6 + i3];
                }

                ad3[l6 - 1] += d16;
            }
            if (Math.abs(ad3[l6 - 1]) > 1.0D)
            {
                d4 = 1.0D / Math.abs(ad3[l6 - 1]);
                for (int j3 = 0; j3 < i; j3++)
                {
                    ad3[j3] = ad3[j3] * d4;
                }

            }
            int k7 = ai[l6 - 1];
            double d8 = ad3[k7 - 1];
            ad3[k7 - 1] = ad3[l6 - 1];
            ad3[l6 - 1] = d8;
        }

        d15 = 0.0D;
        for (int k3 = 0; k3 < i; k3++)
        {
            d15 += Math.abs(ad3[k3]);
        }

        d4 = 1.0D / d15;
        for (int l3 = 0; l3 < i; l3++)
        {
            ad3[l3] = ad3[l3] * d4;
        }

        double d13 = 1.0D;
        for (int i7 = 1; i7 <= i; i7++)
        {
            int l7 = ai[i7 - 1];
            double d9 = ad3[l7 - 1];
            ad3[l7 - 1] = ad3[i7 - 1];
            ad3[i7 - 1] = d9;
            int k8 = Math.min(k, i - i7);
            if (i7 < i && k8 > 0)
            {
                for (int i4 = 0; i4 < k8; i4++)
                {
                    ad3[i7 + i4] = d9 * ad1[(i7 - 1) * i1 + j9 + i4] + ad3[i7 + i4];
                }

            }
            if (Math.abs(ad3[i7 - 1]) <= 1.0D)
            {
                continue;
            }
            d4 = 1.0D / Math.abs(ad3[i7 - 1]);
            for (int j4 = 0; j4 < i; j4++)
            {
                ad3[j4] = ad3[j4] * d4;
            }

            d13 *= d4;
        }

        d15 = 0.0D;
        for (int k4 = 0; k4 < i; k4++)
        {
            d15 += Math.abs(ad3[k4]);
        }

        d4 = 1.0D / d15;
        for (int l4 = 0; l4 < i; l4++)
        {
            ad3[l4] = d4 * ad3[l4];
        }

        d13 *= d4;
        for (int j7 = i; j7 >= 1; j7--)
        {
            if (Math.abs(ad3[j7 - 1]) > Math.abs(ad1[((j7 - 1) * i1 + j9) - 1]))
            {
                d4 = Math.abs(ad1[((j7 - 1) * i1 + j9) - 1]) / Math.abs(ad3[j7 - 1]);
                for (int i5 = 0; i5 < i; i5++)
                {
                    ad3[i5] = ad3[i5] * d4;
                }

                d13 *= d4;
            }
            if (Math.abs(ad1[((j7 - 1) * i1 + j9) - 1]) > d6)
            {
                ad3[j7 - 1] /= ad1[((j7 - 1) * i1 + j9) - 1];
            }
            else
            {
                ad3[j7 - 1] = 1.0D;
            }
            int l8 = Math.min(j7, j9) - 1;
            int i8 = j9 - l8;
            int i9 = j7 - l8;
            double d10 = -ad3[j7 - 1];
            for (int j5 = 0; j5 < l8; j5++)
            {
                ad3[(i9 - 1) + j5] = d10 * ad1[(((j7 - 1) * i1 + i8) - 1) + j5] + ad3[(i9 - 1) + j5];
            }

        }

        d15 = 0.0D;
        for (int k5 = 0; k5 < i; k5++)
        {
            d15 += Math.abs(ad3[k5]);
        }

        d4 = 1.0D / d15;
        for (int l5 = 0; l5 < i; l5++)
        {
            ad3[l5] = ad3[l5] * d4;
        }

        d13 *= d4;
        if (ad4[0] > d6)
        {
            ad2[0] = d13 / ad4[0];
        }
        if (ad2[0] <= 2.2204460492503E-016D)
        {
            Object aobj5[] =
            {
                new Double(ad2[0])
            };
            System.out.println("com.imsl.math : BSpline.IllConditioned");
        }
    }

    private void l2lrb(int i, double ad[], int j, int k, int l, double ad1[], int i1, double ad2[], double ad3[],
            int ai[], double ad4[])
    {
        double ad5[] = new double[1];
        int k1 = k + l + 1;
        if (i <= 0)
        {
            Object aobj[] =
            {
                new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.MatrixOrderTooSmall");
        }
        else if (k < 0 || k >= i)
        {
            Object aobj1[] =
            {
                    new Integer(k), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNLCA");
        }
        else if (l < 0 || l >= i)
        {
            Object aobj2[] =
            {
                    new Integer(l), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNUCA");
        }
        else if (k1 > j)
        {
            Object aobj3[] =
            {
                    new Integer(k1), new Integer(j)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.TooManyRowsInA");
        }
        else if (i1 != 1 && i1 != 2)
        {
            Object aobj4[] =
            {
                new Integer(i1)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadIPATH");
        }
        else
        {
            int j1 = 2 * k + l + 1;
            l2crb(i, ad, j, k, l, ad3, j1, ai, ad5, ad4);
            lfsrb(i, ad3, j1, k, l, ai, ad1, i1, ad2);
            if (ad5[0] <= 2.2204460492503E-016D)
            {
                Object aobj5[] =
                {
                    new Double(ad5[0])
                };
                System.out.println("com.imsl.math : BSpline.IllConditioned");
            }
        }
    }

    private void l2trb(int i, double ad[], int j, int k, int l, double ad1[], int i1, int ai[], double ad2[])
    {
        int k7 = k + l + 1;
        int l7 = 2 * k + l + 1;
        if (i <= 0)
        {
            Object aobj[] =
            {
                new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math: BSpline.MatrixOrderTooSmall");
        }
        else if (k < 0 || k >= i)
        {
            Object aobj1[] =
            {
                    new Integer(k), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNLCA");
        }
        else if (l < 0 || l >= i)
        {
            Object aobj2[] =
            {
                    new Integer(l), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNUCA");
        }
        else if (k7 > j)
        {
            Object aobj3[] =
            {
                    new Integer(k7), new Integer(j)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.TooManyRowsInA");
        }
        else if (l7 > i1)
        {
            Object aobj4[] =
            {
                    new Integer(l7), new Integer(i1)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.TooManyRowsInFac");
        }
        int i4 = 0;
        crbrb(i, ad, j, k, l, ad1, i1, k, l);
        if (k > 0)
        {
            for (int k4 = k7; k4 >= 1; k4--)
            {
                for (int j1 = 0; j1 < i; j1++)
                {
                    ad1[((0 * i1 + k + k4) - 1) + j1 * i1] = ad1[((0 * i1 + k4) - 1) + j1 * i1];
                }

                for (int k1 = 0; k1 < i; k1++)
                {
                    ad1[(k4 - 1) + k1 * i1] = 0.0D;
                }

            }

        }
        double d2 = 4.9406564584124654E-324D;
        double d = 1.7976931348623157E+308D;
        if (d2 * d < 1.0D)
        {
            d2 = 1.0D / d;
        }
        int i7 = i1 - 1;
        for (int l1 = 1; l1 <= i; l1++)
        {
            int k3 = Math.min(l + l1, k7) + k;
            int j5 = Math.max(1, l1 - k);
            int k6 = Math.min(l1 - 1, k) + Math.min(i - l1, l) + 1;
            int i12 = 1;
            int j9 = i7;
            int j11 = k6;
            double d8 = Math.abs(ad1[((j5 - 1) * i1 + k3) - 1]);
            int j12 = j11 * j9;
            int k12 = 1;
            for (int i8 = 1; i8 <= j12; i8 += j9)
            {
                double d9 = Math.abs(ad1[(((j5 - 1) * i1 + k3) - 1) + (i8 - 1)]);
                if (d9 > d8)
                {
                    i12 = k12;
                    d8 = d9;
                }
                k12++;
            }

            int l3 = i12;
            ad2[l1 - 1] = Math.abs(ad1[(((j5 + l3) - 2) * i1 + k3) - l3]);
        }

        int l4 = l + 2;
        int i5 = Math.min(i, k7) - 1;
        for (int l5 = l4; l5 <= i5; l5++)
        {
            int j3 = (k7 + 1) - l5;
            for (int i2 = 0; i2 < (k - j3) + 1; i2++)
            {
                ad1[(((l5 - 1) * i1 + j3) - 1) + i2] = 0.0D;
            }

        }

        int i6 = i5;
        int k5 = 0;
        label0: for (int j6 = 1; j6 <= i - 1; j6++)
        {
            if (++i6 <= i)
            {
                for (int j2 = 0; j2 < k; j2++)
                {
                    ad1[(i6 - 1) * i1 + j2] = 0.0D;
                }

            }
            int j7 = Math.min(k, i - j6);
            int l6 = k7;
            int j4 = j6;
            double d1 = 0.0D;
            for (int k2 = k7; k2 <= k7 + j7; k2++)
            {
                double d5;
                if (ad2[j4 - 1] > d2)
                {
                    d5 = Math.abs(ad1[((j6 - 1) * i1 + k2) - 1]) / ad2[j4 - 1];
                }
                else
                {
                    d5 = Math.abs(ad1[((j6 - 1) * i1 + k2) - 1]);
                }
                j4++;
                if (d5 > d1)
                {
                    d1 = d5;
                    l6 = k2;
                }
            }

            ai[j6 - 1] = (l6 + j6) - k7;
            if (Math.abs(ad1[((j6 - 1) * i1 + l6) - 1]) > d2)
            {
                if (l6 != k7)
                {
                    double d3 = ad1[((j6 - 1) * i1 + l6) - 1];
                    ad1[((j6 - 1) * i1 + l6) - 1] = ad1[((j6 - 1) * i1 + k7) - 1];
                    ad1[((j6 - 1) * i1 + k7) - 1] = d3;
                }
                double d4 = -1D / ad1[((j6 - 1) * i1 + k7) - 1];
                if (j7 != 0)
                {
                    for (int l2 = 0; l2 < j7; l2++)
                    {
                        ad1[(j6 - 1) * i1 + k7 + l2] = d4 * ad1[(j6 - 1) * i1 + k7 + l2];
                    }

                }
                k5 = Math.min(Math.max(k5, l + ai[j6 - 1]), i);
                if ((k7 > 1 || j6 < k5) && (k7 > 1 || j6 < k5))
                {
                    int k9 = i1 - 1;
                    int i10 = i1 - 1;
                    int j8 = k9 >= 0 ? 0 : (k5 - j6 - 1) * k9;
                    int k8 = i10 >= 0 ? 0 : (k5 - j6 - 1) * i10;
                    for (int i9 = 0; i9 < k5 - j6; i9++)
                    {
                        double d6 = ad1[((j6 * i1 + k7) - 2) + j8];
                        ad1[((j6 * i1 + k7) - 2) + j8] = ad1[((j6 * i1 + l6) - 2) + k8];
                        ad1[((j6 * i1 + l6) - 2) + k8] = d6;
                        j8 += k9;
                        k8 += i10;
                    }

                }
                if (j7 == 0 && j6 >= k5)
                {
                    continue;
                }
                int l9 = 1;
                int j10 = i1 - 1;
                int l10 = i1 - 1;
                int i11 = j7;
                int k11 = k5 - j6;
                double d7 = 1.0D;
                int k10 = 1;
                if (j10 < 0)
                {
                    k10 = (-k11 + 1) * j10 + 1;
                }
                int l11 = 1;
                int l8 = 1;
                do
                {
                    if (l8 > k11)
                    {
                        continue label0;
                    }
                    for (int i3 = 0; i3 < i11; i3++)
                    {
                        ad1[((j6 * i1 + (k7 - 1) + l11) - 1) + i3] += d7 * ad1[(j6 * i1 + (k7 - 2) + k10) - 1]
                                * ad1[(j6 - 1) * i1 + k7 + i3 * l9];
                    }

                    k10 += j10;
                    l11 += l10;
                    l8++;
                }
                while (true);
            }
            i4 = j6;
        }

        ai[i - 1] = i;
        if (Math.abs(ad1[((i - 1) * i1 + k7) - 1]) <= d2)
        {
            i4 = i;
        }
        if (i4 != 0)
        {
            throw new IllegalArgumentException("com.imsl.math : BSpline.SingularMatrix");
        }
    }

    private void lfsrb(int i, double ad[], int j, int k, int l, int ai[], double ad1[], int i1, double ad2[])
    {
        int j5 = 2 * k + l + 1;
        if (i <= 0)
        {
            Object aobj[] =
            {
                new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.MatrixOrderTooSmall");
        }
        else if (k < 0 || k >= i)
        {
            Object aobj1[] =
            {
                    new Integer(k), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNLCA");
        }
        else if (l < 0 || l >= i)
        {
            Object aobj2[] =
            {
                    new Integer(l), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNUCA");
        }
        else if (j5 > j)
        {
            Object aobj3[] =
            {
                    new Integer(j5), new Integer(j)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.TooManyRowsInFac");
        }
        for (int j1 = 0; j1 < i; j1++)
        {
            ad2[j1] = ad1[j1];
        }

        int i5 = l + k + 1;
        double d1 = 4.9406564584124654E-324D;
        double d = 1.7976931348623157E+308D;
        if (d1 * d < 1.0D)
        {
            d1 = 1.0D / d;
        }
        if (i1 == 1)
        {
            if (k != 0)
            {
                for (int i3 = 1; i3 <= i - 1; i3++)
                {
                    int k4 = Math.min(k, i - i3);
                    int i4 = ai[i3 - 1];
                    double d2 = ad2[i4 - 1];
                    if (i4 != i3)
                    {
                        ad2[i4 - 1] = ad2[i3 - 1];
                        ad2[i3 - 1] = d2;
                    }
                    for (int k1 = 0; k1 < k4; k1++)
                    {
                        ad2[i3 + k1] = d2 * ad[(i3 - 1) * j + i5 + k1] + ad2[i3 + k1];
                    }

                }

            }
            for (int j3 = i; j3 >= 1; j3--)
            {
                if (Math.abs(ad[((j3 - 1) * j + i5) - 1]) <= d1)
                {
                    System.out.println("com.imsl.math : BSpline.SingularMatrix");
                    return;
                }
            }

            int i2 = l + k;
            int k2 = 1;
            stbsv("U", "N", "N", i, i2, ad, j, ad2, k2);
        }
        else if (i1 == 2)
        {
            for (int k3 = 1; k3 <= i; k3++)
            {
                if (Math.abs(ad[((k3 - 1) * j + i5) - 1]) <= d1)
                {
                    System.out.println("com.imsl.math : BSpline.SingularMatrix");
                    return;
                }
            }

            int j2 = l + k;
            int l2 = 1;
            stbsv("U", "T", "N", i, j2, ad, j, ad2, l2);
            if (k != 0)
            {
                for (int l3 = i - 1; l3 >= 1; l3--)
                {
                    int l4 = Math.min(k, i - l3);
                    double d4 = 0.0D;
                    for (int l1 = 0; l1 < l4; l1++)
                    {
                        d4 += ad[(l3 - 1) * j + i5 + l1] * ad2[l3 + l1];
                    }

                    ad2[l3 - 1] += d4;
                    int j4 = ai[l3 - 1];
                    if (j4 != l3)
                    {
                        double d3 = ad2[j4 - 1];
                        ad2[j4 - 1] = ad2[l3 - 1];
                        ad2[l3 - 1] = d3;
                    }
                }

            }
        }
        else
        {
            Object aobj4[] =
            {
                new Integer(i1)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadIPATH");
        }
    }

    private void nr1rb(int i, double ad[], int j, int k, int l, double ad1[])
    {
        if (i <= 0)
        {
            Object aobj[] =
            {
                new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.MatrixOrderTooSmall");
        }
        int i2 = k + l + 1;
        if (i2 > j)
        {
            Object aobj1[] =
            {
                    new Integer(i2), new Integer(j)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.TooManyRowsInA");
        }
        if (k < 0 || k >= i)
        {
            Object aobj2[] =
            {
                    new Integer(k), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNLCA");
        }
        if (l < 0 || l >= i)
        {
            Object aobj3[] =
            {
                    new Integer(l), new Integer(i)
            };
            throw new IllegalArgumentException("com.imsl.math : BSpline.BadNUCA");
        }
        ad1[0] = 0.0D;
        int l1 = k + 1;
        int i1 = l + 1;
        for (int k1 = 1; k1 <= i; k1++)
        {
            double d1 = 0.0D;
            for (int j1 = 0; j1 < l1; j1++)
            {
                d1 += Math.abs(ad[(k1 - 1) * j + (i1 - 1) + j1]);
            }

            double d = d1;
            ad1[0] = Math.max(ad1[0], d);
            if (i1 > 1)
            {
                i1--;
            }
            if (k1 <= l)
            {
                l1++;
            }
            if (k1 >= i - k)
            {
                l1--;
            }
        }

    }

    private void stbsv(String s, String s1, String s2, int i, int j, double ad[], int k, double ad1[], int l)
    {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        boolean flag5 = false;
        boolean flag6 = false;
        if (s.compareToIgnoreCase("U") == 0)
        {
            flag6 = true;
        }
        if (s.compareToIgnoreCase("L") == 0)
        {
            flag1 = true;
        }
        if (s2.compareToIgnoreCase("U") == 0)
        {
            flag5 = true;
        }
        if (s2.compareToIgnoreCase("N") == 0)
        {
            flag2 = true;
        }
        if (s1.compareToIgnoreCase("N") == 0)
        {
            flag3 = true;
        }
        if (s1.compareToIgnoreCase("T") == 0)
        {
            flag4 = true;
        }
        if (s1.compareToIgnoreCase("C") == 0)
        {
            flag = true;
        }
        if (i < 0)
        {
            Object aobj[] =
            {
                    "n", new Integer(i)
            };
            System.out.println("com.imsl.math : Negative");
            return;
        }
        if (j < 0 && i > 0)
        {
            Object aobj1[] =
            {
                    "ncoda", new Integer(i)
            };
            System.out.println("com.imsl.math : Negative");
            return;
        }
        if (k < j + 1)
        {
            Object aobj2[] =
            {
                    new Integer(k), new Integer(j)
            };
            System.out.println("com.imsl.math : BSpline.LdaGTNCoda");
            return;
        }
        if (l == 0)
        {
            Object aobj3[] =
            {
                    "incx", new Integer(0)
            };
            System.out.println("com.imsl.math : CannotBe");
            return;
        }
        if (!flag3 && !flag4 && !flag)
        {
            Object aobj4[] =
            {
                new String(s1)
            };
            System.out.println("com.imsl.math : BSpline.BadTrans");
            return;
        }
        if (!flag6 && !flag1)
        {
            Object aobj5[] =
            {
                new String(s)
            };
            System.out.println("com.imsl.math : BSpline.BadUplo");
            return;
        }
        if (!flag5 && !flag2)
        {
            Object aobj6[] =
            {
                new String(s2)
            };
            System.out.println("com.imsl.math : BSpline.BadDiag");
            return;
        }
        if (i == 0)
        {
            return;
        }
        int i5 = 1;
        if (l <= 0)
        {
            i5 = 1 - (i - 1) * l;
        }
        if (flag3)
        {
            if (flag6)
            {
                i5 += (i - 1) * l;
                int i4 = i5;
                for (int i3 = i; i3 >= 1; i3--)
                {
                    i5 -= l;
                    if (ad1[i4 - 1] != 0.0D)
                    {
                        if (flag2)
                        {
                            ad1[i4 - 1] /= ad[(i3 - 1) * k + j];
                        }
                        int j5 = Math.max((j + 1) - i3, 0);
                        int i2 = i5 + (j - j5 - 1) * Math.min(-l, 0);
                        for (int i1 = 0; i1 < j - j5; i1++)
                        {
                            ad1[(i2 - 1) + (-(j - j5) + 1) * -l + i1 * -l] += -ad1[i4 - 1]
                                    * ad[(i3 - 1) * k + j5 + (-(j - j5) + 1) * -1 + i1 * -1];
                        }

                    }
                    i4 -= l;
                }

            }
            else
            {
                int j4 = i5;
                for (int j3 = 1; j3 <= i; j3++)
                {
                    i5 += l;
                    if (ad1[j4 - 1] != 0.0D)
                    {
                        if (flag2)
                        {
                            ad1[j4 - 1] /= ad[(j3 - 1) * k + 0];
                        }
                        int k5 = Math.min(j, i - j3);
                        int j2 = i5 + (k5 - 1) * Math.min(l, 0);
                        for (int j1 = 0; j1 < k5; j1++)
                        {
                            ad1[(j2 - 1) + j1 * l] += -ad1[j4 - 1] * ad[(j3 - 1) * k + 1 + j1];
                        }

                    }
                    j4 += l;
                }

            }
        }
        else if (flag6)
        {
            int k4 = i5;
            for (int k3 = 1; k3 <= i; k3++)
            {
                int l5 = Math.max((j + 1) - k3, 0);
                int k2 = i5 + (j - l5 - 1) * Math.min(l, 0);
                double d = 0.0D;
                for (int k1 = 0; k1 < j - l5; k1++)
                {
                    d += ad[(k3 - 1) * k + l5 + k1] * ad1[(k2 - 1) + k1 * l];
                }

                ad1[k4 - 1] -= d;
                if (flag2)
                {
                    ad1[k4 - 1] /= ad[(k3 - 1) * k + j];
                }
                k4 += l;
                if (k3 > j)
                {
                    i5 += l;
                }
            }

        }
        else
        {
            i5 += (i - 1) * l;
            int l4 = i5;
            for (int l3 = i; l3 >= 1; l3--)
            {
                int i6 = Math.min(j, i - l3);
                int l2 = i5 + (i6 - 1) * Math.min(-l, 0);
                double d1 = 0.0D;
                for (int l1 = 0; l1 < i6; l1++)
                {
                    d1 += ad[(l3 - 1) * k + 1 + (-i6 + 1) * -1 + l1 * -1] * ad1[(l2 - 1) + (-i6 + 1) * -l + l1 * -l];
                }

                ad1[l4 - 1] -= d1;
                if (flag2)
                {
                    ad1[l4 - 1] /= ad[(l3 - 1) * k];
                }
                l4 -= l;
                if (i - l3 >= j)
                {
                    i5 -= l;
                }
            }

        }
    }

    public static double sign(double d, double d1)
    {
        double d2 = d >= 0.0D ? d : -d;
        return d1 >= 0.0D ? d2 : -d2;
    }
}